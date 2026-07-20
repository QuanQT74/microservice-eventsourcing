<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Mượn sách thành công</title>
</head>
<body style="margin:0;padding:40px;background:#eef2f7;font-family:Arial,Helvetica,sans-serif;">
<table width="100%" cellpadding="0" cellspacing="0">
    <tr>
        <td align="center">
            <table width="650" cellpadding="0" cellspacing="0"
                   style="background:#ffffff;border-radius:18px;overflow:hidden;box-shadow:0 10px 30px rgba(0,0,0,.12);">
                <tr>
                    <td align="center" style="padding:40px;background:linear-gradient(135deg,#0B1F33,#0066FF);">
                        <h1 style="margin:0;color:white;">LibraStack</h1>
                        <p style="color:#dbeafe;margin:8px 0 0;">Xác nhận mượn sách</p>
                    </td>
                </tr>
                <tr>
                    <td style="padding:40px;">
                        <h2 style="margin-top:0;color:#1e293b;">Xin chào ${name}!</h2>
                        <p style="font-size:16px;color:#475569;line-height:1.7;">
                            Bạn đã mượn thành công cuốn
                            <strong style="color:#0066FF;">${bookName}</strong>.
                        </p>
                        <div style="margin:24px 0;padding:20px;background:#f8fafc;border-left:6px solid #0066FF;border-radius:10px;">
                            <p style="margin:6px 0;color:#334155;"><strong>Mã phiếu:</strong> ${borrowingId}</p>
                            <p style="margin:6px 0;color:#334155;"><strong>Thời gian:</strong> ${date}</p>
                        </div>
                        <p style="color:#64748b;font-size:14px;">Vui lòng trả đúng hạn để giữ quyền mượn tốt.</p>
                    </td>
                </tr>
                <tr>
                    <td align="center" style="background:#111827;padding:28px;">
                        <p style="margin:0;color:#9ca3af;font-size:13px;">© 2026 LibraStack Digital Library</p>
                    </td>
                </tr>
            </table>
        </td>
    </tr>
</table>
</body>
</html>
