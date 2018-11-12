
import tkinter as tk

labels=[]

labels.append(tk.Label(text="B1",bg="green",width=12,height=10,borderwidth=2,relief="solid"))
labels.append(tk.Label(text="B2",bg="green",width=12,height=10,borderwidth=2,relief="solid"))
labels.append(tk.Label(text="A1",bg="green",width=25,height=5,borderwidth=2,relief="solid"))
labels.append(tk.Label(text="A2",bg="green",width=25,height=5,borderwidth=2,relief="solid"))
labels.append(tk.Label(text="A3",bg="green",width=25,height=5,borderwidth=2,relief="solid"))
labels.append(tk.Label(text="A4",bg="green",width=25,height=5,borderwidth=2,relief="solid"))
labels.append(tk.Label())
labels.append(tk.Label(text="T1",bg="green",width=25,height=5,borderwidth=2,relief="solid"))
labels.append(tk.Label(text="T2",bg="green",width=25,height=5,borderwidth=2,relief="solid"))

labels[0].grid(row=0,column=1,padx=1,pady=1)
labels[1].grid(row=0,column=2,padx=1,pady=1)
labels[2].grid(row=1,column=0,padx=1,pady=1)
labels[3].grid(row=2,column=0,padx=1,pady=1)
labels[4].grid(row=3,column=0,padx=1,pady=1)
labels[5].grid(row=4,column=0,padx=1,pady=1)
labels[6].grid(row=5,column=0,padx=1,pady=1)
labels[7].grid(row=6,column=0,padx=1,pady=1)
labels[8].grid(row=7,column=0,padx=1,pady=1)
    

"""Get the index and time of filled park spot.For eg.it is index 2
Replace the index by time and change color
Index Reference
   label 0 = B1
   label 1 = B2
   label 2 = A1
   label 3 =A2
   label 4 = A3
   label 5 = A4
   label 6 = nan
   label 7 = T1
   label 8 = T2"""

labels[2].config(bg="red",text="time parked:00:14:00")


tk.mainloop()