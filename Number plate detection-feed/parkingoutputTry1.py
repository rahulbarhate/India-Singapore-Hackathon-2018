
import tkinter as tk

labels=[]

i=1
for c in range(0,2):
    labels.append(tk.Label(text=i,bg="green",width=25,height=5,padx=5, pady=5))
    labels.append(tk.Label(text=i+1,bg="green",width=25,height=5,padx=10, pady=10))
    i=i+2

q=len(labels)
r=0
for w in range(0,q,1):
    labels[w].grid(row=r,column=0,padx=50,pady=50)
    #labels[w+1].grid(row=r,column=1,padx=50,pady=50)
    r=r+1

#Get the index and time of filled park spot.For eg.it is index 2
    #Replace the index by time and change color
    
#labels[2].config(bg="red",text="time")

tk.mainloop()