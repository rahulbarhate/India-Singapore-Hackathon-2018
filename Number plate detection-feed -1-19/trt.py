import numpy as np
import cv2
import  imutils
cap = cv2.VideoCapture(1)
 #im_no=1

while(True):

    # Capture frame-by-frame
    ret, frame = cap.read()


    # Our operations on the frame come here
    gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)

    ret3, img_thr = cv2.threshold(gray, 125, 255, cv2.THRESH_BINARY)

    cv2.imshow('thresh', img_thr)
    img_edg = cv2.Canny(img_thr, 100, 200)

    kernel = cv2.getStructuringElement(cv2.MORPH_DILATE, (7, 7))
    img_dil = cv2.dilate(img_edg, kernel, iterations=1)

    (somethig_else, contours, hierarchye) = cv2.findContours(img_dil.copy(), 1, 2)
    cnts = sorted(contours, key=cv2.contourArea, reverse=True)[:10]

    for c in cnts:
        # approximate the contour
        peri = cv2.arcLength(c, True)
        approx = cv2.approxPolyDP(c, 0.02 * peri, True)

        # if our approximated contour has four points, then
        # we can assume that we have found our screen
        if len(approx) == 4:
            screenCnt = approx
            break

    # Display the resulting frame
    cv2.imshow('frame',gray)
    cv2.imshow('im_edge', img_edg)
    cv2.imshow("img_dil",img_dil)
    '''
    cv2.imwrite('camX.png',gray)
    # fbU.upload(im_no)
    #im_no=im_no+1 
    '''


    if cv2.waitKey(1) & 0xFF == ord('q'):
        break

# When everything done, release the capture
cap.release()
cv2.destroyAllWindows()