import csv
import random

columns=18
rows=1000000                        #approx 150mb file
for i in range(10):
    rows+=100000                                            #adding ~15mb per iteration
    with open('eggs'+str(i)+'.csv', 'w',newline='\n') as csvfile:
        spamwriter = csv.writer(csvfile, delimiter=',',
                                quotechar='|', quoting=csv.QUOTE_MINIMAL)
        for row in range(rows):
            list=[]
            for column in range(columns):
                list.append(random.randint(0,100000000))
            spamwriter.writerow(list)