import uuid
from cassandra.cluster import Cluster


cluster = Cluster()
session = cluster.connect('videos')

prepared = session.prepare(
    '''
    INSERT INTO files(id, title, data)
    VALUES(?, ?, ?)
    '''
)

f = open('/home/matheus/UP/2023-2/DevWeb/project/video_files/night_of_the_living_dead.mp4', 'rb').read()

session.execute(prepared, (uuid.uuid1(), 'Night Of The Living Dead', f))