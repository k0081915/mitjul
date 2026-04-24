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

$createdBookId = $null
$createdQuoteId = $null

try {
    Write-Step "Checking Swagger/OpenAPI"
    $openApi = Invoke-Json -Path "/v3/api-docs"
    Assert-True ($openApi.info.title -eq "Mitjul API") "OpenAPI title should be Mitjul API"

    Write-Step "Checking tags"
    $tags = Invoke-Json -Path "/api/tags"
    Assert-True ($tags.Count -ge 8) "Tag count should be at least 8"

    Write-Step "Checking seeded books"
    $books = Invoke-Json -Path "/api/books"
    Assert-True ($books.Count -ge 6) "Book count should be at least 6"

    Write-Step "Creating a book"
    $book = Invoke-Json -Method Post -Path "/api/books" -Body @{
        title = "Lv1 검증용 책"
        author = "검증 작성자"
        isbn = "TEST-BOOK-001"
        presetCoverKey = "cover-test"
        status = "READING"
        startedAt = "2026-04-24"
    }
    $createdBookId = $book.id
    Assert-True ($createdBookId -ne $null) "Created book id should not be null"

    Write-Step "Getting created book"
    $bookDetail = Invoke-Json -Path "/api/books/$createdBookId"
    Assert-True ($bookDetail.title -eq "Lv1 검증용 책") "Book title should match"

    Write-Step "Updating created book"
    $updatedBook = Invoke-Json -Method Patch -Path "/api/books/$createdBookId" -Body @{
        status = "PAUSED"
        finishedAt = "2026-04-24"
    }
    Assert-True ($updatedBook.status -eq "PAUSED") "Book status should be PAUSED"

    Write-Step "Creating a quote"
    $quote = Invoke-Json -Method Post -Path "/api/books/$createdBookId/quotes" -Body @{
        page = 42
        content = "검증용 인용문입니다."
        memo = "검증용 메모입니다."
        tagNames = @("영감", "통찰")
    }
    $createdQuoteId = $quote.id
    Assert-True ($createdQuoteId -ne $null) "Created quote id should not be null"

    Write-Step "Getting quotes by book"
    $bookQuotes = Invoke-Json -Path "/api/books/$createdBookId/quotes"
    Assert-True ($bookQuotes.Count -eq 1) "Created book should have one quote"

    Write-Step "Searching quotes"
    $searchResult = Invoke-Json -Path "/api/quotes/search?q=검증용&tag=통찰&bookId=$createdBookId"
    Assert-True ($searchResult.Count -eq 1) "Quote search should return one result"

    Write-Step "Updating quote"
    $updatedQuote = Invoke-Json -Method Patch -Path "/api/quotes/$createdQuoteId" -Body @{
        memo = "수정된 검증용 메모입니다."
        tagNames = @("질문")
    }
    Assert-True ($updatedQuote.memo -eq "수정된 검증용 메모입니다.") "Quote memo should be updated"
    Assert-True ($updatedQuote.tags[0] -eq "질문") "Quote tag should be updated"

    Write-Step "Upserting review"
    $review = Invoke-Json -Method Put -Path "/api/books/$createdBookId/review" -Body @{
        rating = 5
        oneLiner = "검증용 한줄평"
        body = "검증용 리뷰 본문입니다."
    }
    Assert-True ($review.rating -eq 5) "Review rating should be 5"

    Write-Step "Getting review"
    $reviewDetail = Invoke-Json -Path "/api/books/$createdBookId/review"
    Assert-True ($reviewDetail.oneLiner -eq "검증용 한줄평") "Review one-liner should match"

    Write-Step "Checking dashboard summary"
    $summary = Invoke-Json -Path "/api/dashboard/summary?year=2026&month=4"
    Assert-True ($summary.year -eq 2026) "Dashboard year should be 2026"
    Assert-True ($summary.month -eq 4) "Dashboard month should be 4"

    Write-Step "Deleting quote"
    Invoke-RestMethod -Method Delete -Uri "$BaseUrl/api/quotes/$createdQuoteId"
    $createdQuoteId = $null

    Write-Step "Deleting book"
    Invoke-RestMethod -Method Delete -Uri "$BaseUrl/api/books/$createdBookId"
    $createdBookId = $null

    Write-Host "[verify] Backend Lv1 verification passed."
}
finally {
    if ($createdQuoteId -ne $null) {
        try {
            Invoke-RestMethod -Method Delete -Uri "$BaseUrl/api/quotes/$createdQuoteId" | Out-Null
        }
        catch {
            Write-Warning "Failed to cleanup quote ${createdQuoteId}: $($_.Exception.Message)"
        }
    }

    if ($createdBookId -ne $null) {
        try {
            Invoke-RestMethod -Method Delete -Uri "$BaseUrl/api/books/$createdBookId" | Out-Null
        }
        catch {
            Write-Warning "Failed to cleanup book ${createdBookId}: $($_.Exception.Message)"
        }
    }
}
