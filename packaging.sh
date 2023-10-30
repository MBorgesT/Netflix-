#!/bin/bash

nohup \
ffmpeg -i original.mp4 \
-filter_complex \
"[0:v]split=2[v1][v2]; \
[v1]scale=-1:1080[v1out]; [v2]scale=-1:360[v2out]" \
-map "[v1out]" -c:v:0 libx264 -x264-params "nal-hrd=cbr:force-cfr=1" -b:v:0 5M -maxrate:v:0 5M -minrate:v:0 5M -bufsize:v:0 10M -preset slow -g 48 -sc_threshold 0 -keyint_min 48 \
-map "[v2out]" -c:v:1 libx264 -x264-params "nal-hrd=cbr:force-cfr=1" -b:v:1 1M -maxrate:v:1 1M -minrate:v:1 1M -bufsize:v:1 1M -preset slow -g 48 -sc_threshold 0 -keyint_min 48 \
-map a:0 -c:a:0 aac -b:a:0 96k -ac 2 \
-map a:0 -c:a:1 aac -b:a:1 48k -ac 2 \
-f hls \
-hls_time 2 \
-hls_playlist_type vod \
-hls_flags independent_segments \
-hls_segment_type mpegts \
-hls_segment_filename stream_%v-data%02d.ts \
-master_pl_name master.m3u8 \
-var_stream_map "v:0,a:0 v:1,a:1" stream_%v.m3u8 \
> error.log 2> output.log

echo finished > flag.txt
