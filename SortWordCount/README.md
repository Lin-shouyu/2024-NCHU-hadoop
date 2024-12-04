# Hadoop MapReduce 字詞計數排序程式

## 程式說明
這是一個結合了單字計數和排序功能的 Hadoop MapReduce 程式。程式分為兩個 Job 執行：
1. 第一個 Job 統計文本中每個單字出現的次數
2. 第二個 Job 根據單字出現次數進行降序排序

## 程式結構
```
SortWordCount.java
├── CountMap class     - 第一個 Job：將文本分割為單字
├── CountReduce class  - 第一個 Job：統計單字出現次數
├── Map class         - 第二個 Job：準備排序數據
├── Reduce class      - 第二個 Job：輸出排序結果
└── Main method       - 配置並執行兩個 Job
```

## 實現細節

### 第一個 Job (WordCount)
#### Map 階段
- 輸入: `<LongWritable, Text>`
- 輸出: `<Text, IntWritable>`
- 功能: 將輸入文本分割成單字，每個單字計數為 1

#### Reduce 階段
- 輸入: `<Text, Iterable<IntWritable>>`
- 輸出: `<Text, IntWritable>`
- 功能: 統計每個單字的總出現次數

### 第二個 Job (Sort)
#### Map 階段
- 輸入: `<LongWritable, Text>`
- 輸出: `<IntWritable, Text>`
- 功能: 讀取第一個 Job 的結果，交換鍵值對位置以準備排序

#### Reduce 階段
- 輸入: `<IntWritable, Iterable<Text>>`
- 輸出: `<IntWritable, Text>`
- 功能: 輸出按照計數排序的結果

## 輸入格式
- 純文本文件
- 包含任意數量的單字

範例輸入：
```
hello world
hello hadoop
world hadoop
hadoop
```

## 輸出格式
- 每行包含一個單字及其出現次數
- 按照出現次數降序排列

範例輸出：
```
3 hadoop
2 world
2 hello
```

## 技術特點
1. 使用兩個串聯的 MapReduce 作業
2. 使用臨時目錄存儲中間結果
3. 自動清理臨時文件
4. 支持大規模文本處理

## 注意事項
- 輸入文件必須是文本格式
- 輸出目錄不能預先存在
