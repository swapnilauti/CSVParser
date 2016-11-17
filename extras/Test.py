import csv
import random

columns=17
rows=1000000
with open('eggs1.csv', 'w',newline='\n') as csvfile:
    spamwriter = csv.writer(csvfile, delimiter=',',
                            quotechar='|', quoting=csv.QUOTE_MINIMAL)
    for row in range(rows):
        list=[]
        for column in range(columns):
            list.append(random.randint(0,100000000))
        spamwriter.writerow([row]+list)