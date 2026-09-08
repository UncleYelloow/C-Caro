@echo off
chcp 65001 > nul
echo ===================================================
echo   BIÊN DỊCH DỰ ÁN CỜ CARO (GOMOKU) - TUẦN 1-2
echo ===================================================

if not exist bin (
    mkdir bin
)

dir /s /b src\*.java > sources.txt
javac --release 8 -encoding UTF-8 -d bin @sources.txt
del sources.txt

if %errorlevel% equ 0 (
    echo [THÀNH CÔNG] Đã biên dịch toàn bộ mã nguồn vào thư mục bin/
) else (
    echo [THẤT BẠI] Quá trình biên dịch xảy ra lỗi!
)
pause
