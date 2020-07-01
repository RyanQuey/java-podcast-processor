# faster than setup-and-startup (used for development)
# setup-and-startup calls this file though
# in prod though, we'll want gatsby to build the static files ahead of time
# of course if don't need built gatsby (e.g., if using gatsby develop in a separate terminal) can just run 
#     docker-compose -f docker-compose.yml up
# and don't need to build
current_dir=$PWD
cd $current_dir/app/gatsby && \
gatsby build && \
cd $current_dir && \
echo "Now in $current_dir" && \
docker-compose -f docker-compose.yml up
