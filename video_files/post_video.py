import requests

path = '/home/matheus/UP/2023-2/DevWeb/project/video_files/the_letter.mp4'

with open(path, 'rb') as f:
    r = requests.post('http://localhost:8080/contentManagement/uploadMedia', files={'file': f})
    print(r)