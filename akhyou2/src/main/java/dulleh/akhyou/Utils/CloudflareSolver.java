// cloudflare-scrape Java implementation, original: https://github.com/Anorov/cloudflare-scrape
// Original License:
//
// The MIT License (MIT)
//
// Copyright (c) 2015 Anorov
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

// further guidance taken from:
// https://github.com/inorichi/tachiyomi/blob/master/app/src/main/java/eu/kanade/tachiyomi/data/network/CloudflareInterceptor.kt
// Licence available at https://github.com/inorichi/tachiyomi/blob/master/LICENSE (Apache 2.0)

package dulleh.akhyou.Utils;

import com.squareup.duktape.Duktape;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;

public class CloudflareSolver {

    private static final Pattern functionPattern = Pattern.compile("setTimeout\\(function\\(\\)\\{\\s+(var (?:\\w,)+f.+?\\r?\\n[\\s\\S]+?a\\.value =.+?)\r?\n", Pattern.DOTALL);
    private static final Pattern assignPattern = Pattern.compile("a\\.value =(.+?) \\+ .*");
    private static final Pattern stripPattern = Pattern.compile("\\s{3,}[a-z](?: = |\\.).+");
    private static final Pattern jsPattern = Pattern.compile("[\\n\\\\']");


    public static Request solveCloudflare (Response response) throws InterruptedException, IOException, CloudflareException {
        System.out.println("attempting to solve cloudflare");

        Thread.sleep(5000);

        Request request = response.request();
        String host = request.url().host();

        Document doc = Jsoup.parse(response.body().string());
        String challenge = doc.select("[name=jschl_vc]").first().attr("value");
        String challengePass = doc.select("[name=pass]").first().attr("value");

        String function = transformFunction(doc.select("head script").first().html());

        Duktape duktape = Duktape.create();
        Long answer = Long.valueOf(duktape.evaluate(function)) + host.length();
        duktape.close();

        HttpUrl submitUrl = HttpUrl.parse(request.url().uri().getScheme() + "://" + host +"/cdn-cgi/l/chk_jschl").newBuilder()
                .addQueryParameter("jschl_vc", challenge)
                .addQueryParameter("pass", challengePass)
                .addQueryParameter("jschl_answer", String.valueOf(answer))
                .build();

        return new Request.Builder()
                .url(submitUrl)
                .addHeader("Referer", request.url().toString())
                .build();
    }

    private static String transformFunction(String function) throws CloudflareException {
        // We first extract the main javascript function body: function() { --- this part --- }
        Matcher transformer = functionPattern.matcher(function);
        if (!transformer.find()) {
            throw new CloudflareException("Cloudflare evaluation function body could not be extracted.");
        }
        function = transformer.group(1);

        // We then replace the final statement so it returns the correct answer instead of assigning it
        transformer = assignPattern.matcher(function);
        if (!transformer.find()) {
            throw new CloudflareException("Cloudflare function structure changed.");
        }
        function = transformer.replaceFirst("$1;");

        // We then remove unneeded lines that would mess with the execution
        transformer = stripPattern.matcher(function);
        if (!transformer.find()) {
            throw new CloudflareException("Cloudflare variable names changed.");
        }
        function = transformer.replaceAll("");

        // If the function is not already a single line then convert it to that format
        transformer = jsPattern.matcher(function);
        if (transformer.find()) {
            function = transformer.replaceAll("");
        }

        function = function.substring(0, function.length() - 1);
        function = function + ".toString()";

        return function;
    }


}
