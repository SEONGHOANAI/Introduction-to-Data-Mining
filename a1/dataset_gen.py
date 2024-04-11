import numpy as np
import pandas

dataset = pandas.read_csv("g13.csv")
dataset = dataset.drop(columns=["transaction_id"])

dataset_processed = []

# for each rows, if that cell is "True", append that cell's column name to the list
for index, row in dataset.iterrows():
    row_processed = []
    for column in dataset.columns:
        if row[column] == True:
            row_processed.append(column)
    dataset_processed.append(row_processed)


f = open("g13_processed.csv", "w")
for row in dataset_processed:
    f.write(",".join(row) + "\n")
