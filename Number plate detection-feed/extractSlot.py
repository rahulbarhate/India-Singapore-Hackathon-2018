

'''Divides the frame into 6 ( hardcoded ) parts ------Working
    Goal: divide the frames according to dynamically found slots'''




import numpy as np
import cv2


cap = cv2.VideoCapture(0)

while(True):
    # Capture frame-by-frame
    ret, feed = cap.read()

    height, width, ch = feed.shape
    print("width",width)
    print(height)
    n_rows=7
    n_images_per_row = 1
    roi_height=height
    roi_width=width/n_rows



    images = []

    for x in range(0, n_rows):
            tmp_image = feed[80:210, int(x * roi_width):int((x + 1) * roi_width)]
            images.append(tmp_image)

            # Display the resulting sub-frame
    for x in range(0, n_rows):
            cv2.imshow(str(x), images[x])
            cv2.moveWindow(str(x), int(100 + (x * roi_width)),int( 50 + (roi_height)))

    cv2.imshow("Raw Feed",feed)

    if cv2.waitKey(1) & 0xFF == ord('q'):
        break

# When everything done, release the capture
cap.release()
cv2.destroyAllWindows()