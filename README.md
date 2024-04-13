# Introduction-to-Data-Mining HW1

## Prerequisites

- Java JDK or JRE (preferably Java 11 or above.)

You could install this by running the following command on your Linux/MacOS terminal:

```bash
apt-get update
apt-get install -y openjdk-11-jdk
rm -rf /var/lib/apt/lists/*
```

## Usage
sh run.sh [java source file name] [data .csv file] [minsup value in [0,1]]

## Running Examples

### Apriori algorithm
`A1_G13_t1.java`, implementation of apriori algorithm.

```
> java A1_G13_t1 ./data/g13_processed_12k.csv 0.03
Maze Pen 0.030750
Google Metallic Notebook Set 0.030833
Windup Android 0.031250
Google Men's Bike Short Sleeve Tee Charcoal 0.031833
26 oz Double Wall Insulated Bottle 0.033083
Sport Bag 0.033417
Google Men's 100% Cotton Short Sleeve Hero Tee White 0.034667
YouTube Custom Decals 0.034750
Google Men's Vintage Badge Tee Black 0.036667
Google Men's 100% Cotton Short Sleeve Hero Tee Black 0.041333
Google Twill Cap 0.044667
Google 22 oz Water Bottle 0.056083
Google Sunglasses 0.056583
Google Laptop and Cell Phone Stickers 0.065583
```

### FP-growth algorithm
`A1_G13_t2.java`, implementation of FP-growth algorithm.

```bash
% sh run.sh A1_G13_t2.java data/g13_processed_12k.csv 0.03
```

Result

```
File A1_G13_t2.java exists!
Maze Pen 0.0307500
Google Metallic Notebook Set 0.0308333
Windup Android 0.0312500
Google Men's Bike Short Sleeve Tee Charcoal 0.0318333
26 oz Double Wall Insulated Bottle 0.0330833
Sport Bag 0.0334167
Google Men's 100% Cotton Short Sleeve Hero Tee White 0.0346667
YouTube Custom Decals 0.0347500
Google Men's Vintage Badge Tee Black 0.0366667
Google Men's 100% Cotton Short Sleeve Hero Tee Black 0.0413333
Google Twill Cap 0.0446667
Google 22 oz Water Bottle 0.0560833
Google Sunglasses 0.0565833
Google Laptop and Cell Phone Stickers 0.0655833
```

## Dataset
"data" contains "g13_processed_12k.csv", transaction dataset.
