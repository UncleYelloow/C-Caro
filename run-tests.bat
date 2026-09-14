@echo off
chcp 65001 > nul
echo ===================================================
echo   CHẠY BỘ TEST KIỂM THỬ THUẬT TOÁN VÀ LOGIC
echo ===================================================

if not exist bin\com\vnuk\caro\WinCheckerVerification.class (
    echo Đang biên dịch mã nguồn trước khi chạy test...
    call build.bat
)

echo [1/2] Kiểm thử Logic Bàn cờ và WinChecker:
java "-Dfile.encoding=UTF-8" -cp bin com.vnuk.caro.WinCheckerVerification

echo.
echo [2/2] Kiểm thử Thuật toán AI, Chặn nước và Alpha-Beta:
java "-Dfile.encoding=UTF-8" -cp bin com.vnuk.caro.AIAlgorithmTest

pause
