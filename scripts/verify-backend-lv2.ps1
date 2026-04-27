param(
    [string]$BaseUrl = "http://localhost:8080"
)

$ErrorActionPreference = "Stop"

function Write-Step {
    param([string]$Message)
    Write-Host "[verify] $Message"
}

function Invoke-Json {
    param(
        [string]$Method = "Get",
        [string]$Path,
        [object]$Body = $null
    )

    $uri = "$BaseUrl$Path"
    if ($null -eq $Body) {
        return Invoke-RestMethod -Method $Method -Uri $uri
    }

    $json = $Body | ConvertTo-Json -Depth 10
    return Invoke-RestMethod -Method $Method -Uri $uri -ContentType "application/json" -Body $json
}

function Assert-True {
    param(
        [bool]$Condition,
        [string]$Message
    )

    if (-not $Condition) {
        throw "Assertion failed: $Message"
    }
}

Write-Step "Checking order preview"
$preview = Invoke-Json -Method Post -Path "/api/orders/preview" -Body @{
    periodStart = "2026-04-01"
    periodEnd = "2026-04-30"
    coverStyle = "MINIMAL"
    ownerName = "검증 사용자"
}
Assert-True ($preview.bookCount -ge 1) "Preview should include at least one book"
Assert-True ($preview.quoteCount -ge 1) "Preview should include at least one quote"

Write-Step "Creating order"
$order = Invoke-Json -Method Post -Path "/api/orders" -Body @{
    periodStart = "2026-04-01"
    periodEnd = "2026-04-30"
    coverStyle = "MINIMAL"
    ownerName = "검증 사용자"
}
Assert-True ($order.id -ne $null) "Created order id should not be null"
Assert-True ($order.orderNumber -like "MJ-*") "Order number should start with MJ-"
Assert-True ($order.status -eq "PENDING") "Created order should start as PENDING"
Assert-True ($order.items.Count -ge 1) "Created order should have items"

Write-Step "Getting order list"
$orders = Invoke-Json -Path "/api/orders"
Assert-True (($orders | Where-Object { $_.id -eq $order.id }).Count -eq 1) "Order list should include created order"

Write-Step "Getting order detail"
$orderDetail = Invoke-Json -Path "/api/orders/$($order.id)"
Assert-True ($orderDetail.orderNumber -eq $order.orderNumber) "Order detail number should match"

Write-Step "Updating order status"
$updatedOrder = Invoke-Json -Method Patch -Path "/api/orders/$($order.id)/status" -Body @{
    status = "PROCESSING"
}
Assert-True ($updatedOrder.status -eq "PROCESSING") "Order status should be PROCESSING"

Write-Host "[verify] Backend Lv2 verification passed."
