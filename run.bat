@echo off
chcp 65001 > nul
echo ===================================================
echo   KHỞI CHẠY GAME CỜ CARO (GOMOKU)
echo ===================================================

if not exist bin\com\vnuk\caro\Main.class (
    echo Chưa biên dịch mã nguồn. Đang tự động biên dịch...
    call build.bat
)

start javaw "-Dfile.encoding=UTF-8" -cp bin com.vnuk.caro.Main
