# https://stackoverflow.com/a/8880633/6952495
declare -a tables=(
	"search_queries_by_term" 
	"podcasts_by_language" 
	"episodes_by_podcast"
)

## now loop through the above array
printf "\n\n**********************"
printf "\n***NOW CREATING ALL***"
for i in "${tables[@]}"
do
  printf "\n\nCreating index for: $i"
  printf "\ncalling: curl -XPUT -H 'Content-Type: application/json' \"http://localhost:9200/$i\" -d @./$i.json\n"
  echo "---"
	curl -XPUT -H 'Content-Type: application/json' "http://localhost:9200/$i" -d @./$i.json
   # or do whatever with individual element of the array
done

# You can access them using printf "${arr[0]}", "${arr[1]}" also
