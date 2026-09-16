@echo off
echo ===================================================
echo   BIEN DICH DU AN CO CARO (GOMOKU)
echo ===================================================

if not exist bin mkdir bin

powershell -NoProfile -Command "(Get-ChildItem -Recurse -Filter *.java src | Resolve-Path -Relative) -replace '\\', '/' | Set-Content sources.txt"
javac --release 8 -encoding UTF-8 -d bin @"sources.txt"
set BUILD_STATUS=%errorlevel%
if exist sources.txt del sources.txt

if %BUILD_STATUS% equ 0 (
    echo [THANH CONG] Da bien dich toan bo ma nguon vao thu muc bin/
) else (
    echo [THAT BAI] Qua trinh bien dich xay ra loi!
)
pause
