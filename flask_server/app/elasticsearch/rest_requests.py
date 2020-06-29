from flask import make_response
import requests
import os
import json

ELASTICSEARCH_URL = os.environ.get("ELASTICSEARCH_URL")

# TODO use python ES client instead
def search(es_index, req):
    """
    makes http request to our ES server
    TODO will need to make sure to attach the rest of the url received from the browser, e.g., _search and the payload
    TODO add authentication for security
    """

    print(req)
    url = os.path.join(ELASTICSEARCH_URL, es_index, "_search")

    print("ES url:", url)
    print("ES data:", req.data)
    print("ES json:", req.json)
    data_json = json.dumps(req.json)
    print("in curl it would be:\n", f"curl -XPOST {url} -d \"{data_json}\" -H \"Content-Type: application/json\" ", "\n")
    result = requests.post(url, json.dumps(req.json), headers={"content-type": "application/json"})

    # TODO better error handling
    if result.status_code is not 200:
        print("ERROR:", result.content)
    else:
        print("ES result SUCCESS:", result)

    resp = make_response(result.json(), result.status_code)

    return resp
