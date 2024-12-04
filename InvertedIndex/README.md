# Hadoop MapReduce 倒排索引程式

## 程式說明
這是一個使用 Hadoop MapReduce 實現的倒排索引程式。倒排索引是一種常用於搜尋引擎的索引方式，它記錄每個單字出現在哪些文件中。程式會讀取包含文件ID和內容的輸入，建立單字到文件ID的映射關係。

## 程式結構
```
InvertedIndex.java
├── Map class    - 解析輸入並產生單字-文件ID對
├── Reduce class - 整合單字在不同文件中的出現位置
└── Main method  - 程式配置和執行
```

## 實現細節

### Map 階段
- 輸入: `<LongWritable, Text>`
  - 每行的格式為：`文件ID 單字1 單字2 ...`
- 輸出: `<Text, IntWritable>`
  - 單字作為鍵，文件ID作為值

Map 階段的主要工作：
1. 從每行讀取文件ID
2. 解析該行中的所有單字
3. 為每個單字發出 `<單字, 文件ID>` 鍵值對

### Reduce 階段
- 輸入: `<Text, Iterable<IntWritable>>`
- 輸出: `<Text, Text>`
  - 輸出格式：`單字 tab檔案ID1,檔案ID2,...`

Reduce 階段的主要工作：
- 使用 HashSet 去除重複的文件ID
- 將文件ID列表轉換為逗號分隔的字串

## 輸入格式
- 每行以文件ID開始，後接該文件包含的單字
- 文件ID必須是整數

範例輸入：
```
1 hello world hadoop
2 hello hadoop
3 world hadoop program
```

## 輸出格式
- 每行包含一個單字及其出現的所有文件ID
- 文件ID以逗號分隔

範例輸出：
```
hadoop  1,2,3
hello   1,2
program 3
world   1,3
```

## 技術特點
1. 使用 HashSet 實現文件ID去重
2. 適合處理大規模文本索引
3. 輸出格式便於後續檢索使用

## 注意事項
- 輸入的第一個token必須是有效的文件ID
- 輸出目錄不能預先存在
