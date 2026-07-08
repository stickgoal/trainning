#!/bin/bash
# 代码风格检查脚本
# 使用方式：bash check_style.sh <java文件路径>

if [ -z "$1" ]; then
    echo "用法：bash check_style.sh <java文件路径>"
    exit 1
fi

FILE="$1"

if [ ! -f "$FILE" ]; then
    echo "错误：文件 $FILE 不存在"
    exit 1
fi

echo "=========================================="
echo "代码风格检查：$FILE"
echo "=========================================="

# 检查行长度（超过120字符）
echo ""
echo "【行长度检查】超过120字符的行："
grep -n '.\{121\}' "$FILE" | while read line; do
    echo "  $line"
done

# 检查是否有 System.out.println
echo ""
echo "【日志检查】使用了 System.out.println 的行："
grep -n 'System\.out\.println' "$FILE" | while read line; do
    echo "  $line"
done

# 检查是否有 printStackTrace
echo ""
echo "【异常处理检查】使用了 printStackTrace 的行："
grep -n 'printStackTrace' "$FILE" | while read line; do
    echo "  $line"
done

echo ""
echo "=========================================="
echo "检查完成"
echo "=========================================="