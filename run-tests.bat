@echo off
chcp 65001 > nul
echo ===================================================
echo   CHẠY BỘ TEST KIỂM THỬ THUẬT TOÁN VÀ LOGIC
echo ===================================================

if not exist bin\com\vnuk\caro\WinCheckerVerification.class (
    echo Đang biên dịch mã nguồn trước khi chạy test...
    call build.bat
)

echo [1/3] Kiểm thử Logic Bàn cờ và WinChecker (Tuần 1-2):
java "-Dfile.encoding=UTF-8" -cp bin com.vnuk.caro.WinCheckerVerification

echo.
echo [2/3] Kiểm thử Chế độ Người vs Người & Luật Caro (Tuần 3-4):
java "-Dfile.encoding=UTF-8" -cp bin com.vnuk.caro.PvPModeTest

echo.
echo [3/3] Kiểm thử Thuật toán AI, Chặn nước và Alpha-Beta (Tuần 5-7):
java "-Dfile.encoding=UTF-8" -cp bin com.vnuk.caro.AIAlgorithmTest

pause
