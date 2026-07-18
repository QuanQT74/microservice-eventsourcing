$doc = [xml](Get-Content "d:\Objects\eventSourcing\docx_temp\word\document.xml")
$texts = $doc.GetElementsByTagName("w:t") | Select-Object -ExpandProperty InnerText
$texts | Out-String -Width 50000 | Set-Content "d:\Objects\eventSourcing\docx_text.txt"
Write-Host "Extracted text to docx_text.txt"
