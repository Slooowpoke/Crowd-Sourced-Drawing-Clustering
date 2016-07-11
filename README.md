# Crowd-Sourced-Drawing-Clustering

There was a previous repo which contained commits from September to March, however during the Easter Holidays I scrapped everything and started over with a huge refactor including threading some processes to make it easier on the visualiser. 

The program uses Slick2D and LWJGL to run and utlise the graphics card. It also made building visual interfaces much much easier and gave more freedom and time for me to create the algorithms instead.

The two clustering algorithms used are the self-organising map (SOM) created by Teuvo Kohonen and K-Means (a statistical clustering method).

The program is also a visualiser for both clustering methods and you can view an earlier version of the visualisation <a href="">here..</a> Both sorting algorithms work well (ish) and have numerous parameters that can be adjusted from the properties files provided in /data/.

I achieved <b>80/100</b> for my Viva and <b>80/100</b> for the program and report provided. To read the report <a href="http://malicoxon.co.uk/university/dissertation/Crowd-sourceddrawingclusteringandsegmentationusingNeuralComputing.pdf">click here.</a>


During the dissertation I struggled towards the end with a final calculation, the adjusted rand index which I hope in the next coming months to implement fully and include in this repo. 
