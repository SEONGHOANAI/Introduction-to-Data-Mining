import numpy as np
import pandas

dataset = pandas.read_csv("./data/g13.csv")
dataset = dataset.drop(columns=["transaction_id"])

dataset_processed = []

# for each rows, if that cell is "True", append that cell's column name to the list
for index, row in dataset.iterrows():
    row_processed = []
    for column in dataset.columns:
        if row[column] == True:
            row_processed.append(column)
    dataset_processed.append(row_processed)

np.random.shuffle(dataset_processed)

for i in range(1, 13):
    _i = i * 1000
    f = open("./data/g13_processed_" + str(i) + "k.csv", "w")
    for row in dataset_processed[0:_i]:
        f.write(",".join(row) + "\n")
