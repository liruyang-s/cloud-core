<#
.SYNOPSIS
    One-click Nacos config import script (out-of-the-box for the scaffold).

.DESCRIPTION
    Steps: create namespaces (dev/test/prod) -> login -> publish all configs under config/nacos/.
    Config file naming rule: {GROUP}__{dataId}, e.g. DEFAULT_GROUP__micro-user.yaml

.PARAMETER ServerAddr
    Nacos address, default 127.0.0.1:8848

.PARAMETER Username / Password
    Nacos credentials, default nacos/nacos (ignored when auth is disabled)

.EXAMPLE
    .\scripts\import-nacos-config.ps1
    .\scripts\import-nacos-config.ps1 -ServerAddr 192.168.1.10:8848 -Username nacos -Password nacos
#>
param(
    [string]$ServerAddr = "127.0.0.1:8848",
    [string]$Username   = "nacos",
    [string]$Password   = "nacos"
)

$ErrorActionPreference = "Stop"
$baseUrl   = "http://$ServerAddr/nacos"
$configDir = Join-Path $PSScriptRoot "..\config\nacos"

# namespaceId must match the default value of NACOS_NAMESPACE in bootstrap.yml
$namespaces = @(
    @{ id = "dev";  name = "dev";  desc = "development" },
    @{ id = "test"; name = "test"; desc = "testing" },
    @{ id = "prod"; name = "prod"; desc = "production" }
)

Write-Host "==> Nacos: $baseUrl" -ForegroundColor Cyan

# 1. Login (skip when auth is disabled)
$token = ""
try {
    $login = Invoke-RestMethod -Uri "$baseUrl/v1/auth/login" -Method Post -Body @{ username = $Username; password = $Password }
    $token = $login.accessToken
    Write-Host "==> Login OK" -ForegroundColor Green
} catch {
    Write-Host "==> Auth disabled or login failed, try anonymous: $($_.Exception.Message)" -ForegroundColor Yellow
}
$authQuery = if ($token) { "accessToken=$token" } else { "" }

function Invoke-Nacos {
    param([string]$Uri, [string]$Method = "Get", $Body = $null)
    $sep = if ($Uri -match "\?") { "&" } else { "?" }
    $fullUri = if ($authQuery) { "$Uri$sep$authQuery" } else { $Uri }
    if ($Body) {
        return Invoke-RestMethod -Uri $fullUri -Method $Method -Body $Body
    }
    return Invoke-RestMethod -Uri $fullUri -Method $Method
}

# 2. Create namespaces (skip if exists)
foreach ($ns in $namespaces) {
    try {
        $result = Invoke-Nacos -Uri "$baseUrl/v1/console/namespaces" -Method Post -Body @{
            customNamespaceId = $ns.id
            namespaceName     = $ns.name
            namespaceDesc     = $ns.desc
        }
        Write-Host "==> Namespace [$($ns.id)] create result: $result" -ForegroundColor Green
    } catch {
        Write-Host "==> Namespace [$($ns.id)] create failed (may already exist): $($_.Exception.Message)" -ForegroundColor Yellow
    }
}

# 3. Publish configs (file name rule: {GROUP}__{dataId})
$files = Get-ChildItem -Path $configDir -File
if ($files.Count -eq 0) {
    Write-Host "==> config/nacos/ is empty, nothing to import" -ForegroundColor Red
    exit 1
}

$success = 0
foreach ($file in $files) {
    $parts = $file.BaseName -split "__", 2
    if ($parts.Count -ne 2) {
        Write-Host "==> Skip (file name must be GROUP__dataId): $($file.Name)" -ForegroundColor Yellow
        continue
    }
    $group  = $parts[0]
    $dataId = $parts[1] + $file.Extension
    $content = Get-Content -Path $file.FullName -Raw -Encoding UTF8

    $type = switch -Regex ($file.Extension) {
        "\.yaml$"       { "yaml" }
        "\.yml$"        { "yaml" }
        "\.properties$" { "properties" }
        "\.json$"       { "json" }
        default         { "text" }
    }

    try {
        $result = Invoke-Nacos -Uri "$baseUrl/v1/cs/configs" -Method Post -Body @{
            tenant  = "dev"
            dataId  = $dataId
            group   = $group
            content = $content
            type    = $type
        }
        if ($result -eq "true") {
            Write-Host "==> [dev] $group / $dataId published" -ForegroundColor Green
            $success++
        } else {
            Write-Host "==> [dev] $group / $dataId publish failed: $result" -ForegroundColor Red
        }
    } catch {
        Write-Host "==> [dev] $group / $dataId publish error: $($_.Exception.Message)" -ForegroundColor Red
    }
}

Write-Host ""
Write-Host "==> Done: $success / $($files.Count) succeeded" -ForegroundColor Cyan
Write-Host "==> Console: $baseUrl/#/configurationManagement?namespaceShowName=dev" -ForegroundColor Cyan
