# https://stackoverflow.com/a/8880633/6952495
declare -a tables=(
	"search_queries_by_term" 
	"podcasts_by_language" 
	"episodes_by_podcast"
)

## now loop through the above array
printf "************************"
printf "\n***NOW DESTROYING ALL ES INDICES***"
for i in "${tables[@]}"
do
  printf "\n\nDestroying index for: $i"
  printf "\ncalling: curl -XDELETE \"http://localhost:9200/$i\"\n"
  echo "---"
	curl -XDELETE "http://localhost:9200/$i"
   # or do whatever with individual element of the array
done

# You can access them using printf "${arr[0]}", "${arr[1]}" also
