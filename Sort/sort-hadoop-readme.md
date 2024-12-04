# Hadoop MapReduce 排序程式

## 程式說明
這是一個使用 Hadoop MapReduce 框架實現的數字排序程式。程式可以讀取輸入文件中的數字，並將這些數字進行排序後輸出。此實現利用了 Hadoop MapReduce 的特性，其中鍵值對自動排序的機制來完成排序功能。

## 程式結構
```
Sort.java
├── Map class    - 將輸入文本轉換為數字
├── Reduce class - 輸出排序後的結果
└── Main method  - 程式配置和執行
```

## 實現細節

### Map 階段
- 輸入: `<LongWritable, Text>`
  - `LongWritable`: 輸入行的位置偏移量
  - `Text`: 輸入的文本行
- 輸出: `<IntWritable, NullWritable>`
  - `IntWritable`: 解析後的整數值
  - `NullWritable`: 空值，因為排序只需要關注鍵值

Map 階段的主要工作：
1. 讀取每一行文本
2. 將文本分割成單個數字
3. 將每個數字轉換為 IntWritable 類型
4. 發出 `<數字, NullWritable>` 鍵值對

### Reduce 階段
- 輸入: `<IntWritable, Iterable<NullWritable>>`
- 輸出: `<IntWritable, NullWritable>`

Reduce 階段的主要工作：
- 接收已排序的鍵值對
- 直接輸出鍵值，保持排序順序

## 輸入格式
- 文本文件
- 每行包含一個或多個以空格分隔的整數

範例輸入：
```
23 45 12
67 89 34
1 78 90
```

## 輸出格式
- 排序後的整數，每行一個數字
- 按升序排列

範例輸出：
```
1
12
23
34
45
67
78
89
90
```

## 技術特點
1. 使用 `NullWritable` 作為值類型來節省空間
2. 利用 MapReduce 框架自身的排序機制
3. 支持大規模數據處理

## 注意事項
- 輸入文件中的數字必須是有效的整數
- 輸出目錄不能預先存在
