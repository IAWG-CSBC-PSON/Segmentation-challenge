# my helper functions

# must have opencv installed
import cv2
import numpy

def cmask(radius):
    kernel = numpy.zeros((2 * radius + 1, 2 * radius + 1), numpy.uint8)
    y, x = numpy.ogrid[-radius:radius + 1, -radius:radius + 1]
    mask = x ** 2 + y ** 2 <= radius ** 2
    kernel[mask] = 1
    return kernel

# example usage for tophat (rolling ball) filtering
# bc_img = cv2.morphologyEx(img, cv2.MORPH_TOPHAT, cmask(20))

