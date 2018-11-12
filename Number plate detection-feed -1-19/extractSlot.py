

'''Divides the frame into 7 ( hardcoded ) parts ------Working AND cropped the top bottom
    Goal: divide the frames according to dynamically found slots'''

import numpy as np
import cv2
import pyrebase
config = {
        "apiKey": "AIzaSyA9klvBp9ZO99tgibJp9rhPI4Ub0362C68",
        "authDomain": "singapore-a572e.firebaseapp.com",
        "databaseURL": "https://singapore-a572e.firebaseio.com",
        "projectId": "singapore-a572e",
        "storageBucket": "singapore-a572e.appspot.com",
        "messagingSenderId": "359205745104"
}
firebase = pyrebase.initialize_app(config)
database = firebase.database()



# Trained XML classifiers describes some features of some object we want to detect
car_cascade = cv2.CascadeClassifier('cars.xml')

cap = cv2.VideoCapture(0)

while(True):
    # Capture frame-by-frame
    ret, feed = cap.read()

    height, width, ch = feed.shape
    #print("width",width)
    #print(height)
    n_rows=4
    n_images_per_row = 1
    roi_height=height
    roi_width=380/n_rows



    images = []

    for x in range(0, n_rows):
            tmp_image = feed[250:400, int(x * roi_width):int((x + 1) * roi_width)]
            images.append(tmp_image)

            # Display the resulting sub-frame
    for x in range(0, n_rows):
        cv2.imshow(str(x), images[x])
        cv2.moveWindow(str(x), int(300 + (x * roi_width)),int(180 + (roi_height)))

        # convert to gray scale of each frames
        gray = cv2.cvtColor(feed, cv2.COLOR_BGR2GRAY)

        # Detects cars of different sizes in the input image
        cars = car_cascade.detectMultiScale(gray, 1.1, 1)

        # To draw a rectangle in each cars
        vacant = [[0, 0 , 0, 0], [0, 0, 0], [0, 0]]     
        # a1  1
        # a2  2
        # a3  3
        # a4  4
        # b1  5
        # b2  6
        # b3  7
        # c1  8
        # c2  9   
        for (x, y, w, h) in cars:
            cv2.rectangle(feed, (x, y), (x + w, y + h), (0, 0, 255), 2)
            print(x+h)
            if x+h<=87:
                vacant[0][0] = 1
            elif x+h<=160:
                vacant[0][1] = 1 
            elif x+h<= 251:
                vacant[0][2] = 1
            elif x+h<423:
                vacant[0][3] = 1 # complete these conditions... mag test karu
        

        database.child("ParkingLot1").set(str(vacant))
                     
    #finding out which slots are occupied

    cv2.imshow("Raw Feed", feed)

    if cv2.waitKey(1) & 0xFF == ord('q'):
        break

# When everything done, release the capture
cap.release()
cv2.destroyAllWindows()