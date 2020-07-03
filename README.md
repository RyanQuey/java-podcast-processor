# java-podcast-processor
Personal tool to grab podcast data related to several topics I'm interested in, store them, and process using Airflow, Kafka, Spark, and Cassandra. Some of these tools are a little overkill for this project...but it's a learning project too, so just having fun with it
See README files in individual directories for how to use (though some or all need updating)

## Start Everything
- Start everything with: `./scripts/startup/start-every-compose.sh`
- Open up React Gatsby project (serving searchkit) via flask app at http://www.local.test:5000/

## Development
- If made changes to java code and want to rebuild what docker is running, run

    `./scripts/startup/start-every-compose.sh rebuild`

# Released under MIT License

Copyright (c) 2020 Ryan Quey.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
