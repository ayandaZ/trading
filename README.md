# Trader
This is a test project to see if NR7 stock selections in NSE gives out high volatile stocks only.

# Installation
Download the latest stable release. 

# Run
Download last 7 days of bhavcopy Report from the url - https://www1.nseindia.com/products/content/equities/equities/archieve_eq.htm
Navigate to the folder containing the jar. </br>
Run the following command in the console "java -cp Trader.jar Trader nr7 <baseDir containing bhavcopy reports> <delta>".

NOTE: delta is a positive integer which is the previous day's minimum of the last 7 day's difference between the high and low.
