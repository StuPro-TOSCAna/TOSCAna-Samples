// Copyright © 2018 Christian Müller <cmueller.dev@gmail.com>
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
// THE SOFTWARE.

package main

import (
	"flag"
	"fmt"
	"github.com/gin-gonic/gin"
	"os"
	"runtime"
	"strings"
	"time"
)

func main() {
	port := flag.Int("port", 8080, "The Port to Run the server on")
	flag.Parse()
	gin.SetMode(gin.ReleaseMode)
	r := gin.Default()
	r.GET("/", handleRequest)
	r.Run(fmt.Sprintf(":%d", *port))
}

func handleRequest(ctx *gin.Context) {
	hostname, err := os.Hostname()
	panicOnErr(err)
	systime := time.Now()
	environment := "<table border=\"1\"><tr><th>Key</th><th>Value</th></tr>"
	for _, v := range os.Environ() {
		split := strings.Split(v, "=")
		environment += fmt.Sprintf("<tr><td>%s</td><td>%s</td></tr>", split[0], split[1])
	}
	environment += "</table>"
	content := fmt.Sprintf(`<head>
					<title>Gopher says Hi</title>
					<style>
					body {font-family: Arial;}
					</style>
				</head>
				<body>
				<h1>Hello World</h1>
				<p>
					My Hostname is: %s</br>
					I have %d CPUs</br>
					My pid is: %d</br>
					My uid is: %d</br>
					My System Time is: %s</br>
					My Environment Variables are:</br>%s
				</p>
				</body>`,
		hostname,
		runtime.NumCPU(),
		os.Getpid(),
		os.Getuid(),
		systime.String(),
		environment)
	content = strings.Replace(content, "\t", "", -1)
	ctx.Data(200, "text/html", []byte(content))
}

func panicOnErr(err error) {
	if err != nil {
		panic(err)
	}
}
