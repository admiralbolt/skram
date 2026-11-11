import cv2
import glob
import random

SPOT_ONE = [
  (6, 6),
  (6, 7),
  (6, 8),
  (7, 5),
  (7, 9),
  (8, 6),
  (8, 7,),
  (8, 8)
]

SPOT_TWO = [
  (7, 6),
  (7, 8)
]

CENTER = [(7, 7)]

LIGHT_GRAY_AMOUNT = 14


GRAY_PIXELS = [
  (5, 6),
  (5, 8),
  (5, 9),
  (6, 3),
  (6, 4),
  (6, 11),
  (7, 2),
  (7, 12),
  (8, 3),
  (8, 10),
  (8, 11),
  (9, 5),
  (9, 6),
  (9, 8),
  (4, 5),
  (4, 6),
  (4, 7),
  (4, 8),
  (4, 9),
  (5, 2),
  (5, 3),
  (5, 4),
  (5, 5),
  (5, 7),
  (5, 10),
  (5, 11),
  (5, 12),
  (6, 1),
  (6, 2),
  (6, 5),
  (6, 9),
  (6, 10),
  (6, 12),
  (6, 13),
  (7, 1),
  (7, 3),
  (7, 4),
  (7, 10),
  (7, 11),
  (7, 13),
  (8, 1),
  (8, 2),
  (8, 4),
  (8, 5),
  (8, 9),
  (8, 12),
  (8, 13),
  (9, 2),
  (9, 3),
  (9, 4),
  (9, 7),
  (9, 9),
  (9, 10),
  (9, 11),
  (9, 12),
  (10, 5),
  (10, 6),
  (10, 7),
  (10, 8),
  (10, 9)
]


for f in glob.glob("music_disc*.png"):
  im = cv2.imread(f, cv2.IMREAD_UNCHANGED)

  # It's a 16x16 image, and we want to generate two colors, and update two
  # blocks of pixels accordingly.

  for pixel_list in [SPOT_ONE, SPOT_TWO, CENTER]:
    color = (random.randint(80, 200), random.randint(80, 200), random.randint(80, 200), 255)
    for pixel in pixel_list:
      im[pixel] = color

  light_gray = (random.randint(65, 70), random.randint(65, 70), random.randint(65, 70), 255)
  dark_gray = (random.randint(37, 40), random.randint(37, 40), random.randint(37, 40), 255)

  random.shuffle(GRAY_PIXELS)
  
  for pixel in GRAY_PIXELS[:LIGHT_GRAY_AMOUNT]:
    im[pixel] = light_gray

  for pixel in GRAY_PIXELS[LIGHT_GRAY_AMOUNT:]:
    im[pixel] = dark_gray

  cv2.imwrite(f, im)
