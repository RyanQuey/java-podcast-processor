import urllib.request

# leave spaces here for easy reading, but change when putting into file or search terms
search-terms = [
    "data engineering",
]

"""
for each term, ask itunes for results
"""
for term in search-terms:
    # convert term to url encoded, using + (as according to itunes examples)
    term_in_query = re.sub(r"\s+", "\+", term)

    # set version, language and country in case their defaults change
    # set limit to 200, let's just get all of it (default: 50)
    contents = urllib.request.urlopen(f"https://itunes.apple.com/search?media=podcast&term={term}&limit=200&version=2&lang=en_us&country=US").read()
    print(contents)
    # use `with` to prevent leaving file open after code runs
    term_for_file = re.sub(r"\s+", "-", term)
    with open(f"{term}-podcasts.json", "r+") as file:
        file.write(contents)
        file.close()

    # check file is closed
    file.closed

