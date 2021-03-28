package com.caputo.boxshare.service.engines;

import com.caputo.boxshare.builder.SearchResultBuilder;
import com.caputo.boxshare.entity.SearchResult;
import com.caputo.boxshare.enumerable.SearchMethod;
import java.util.List;
import java.util.Optional;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/** A class responsible for querying the torrent search engine CorsaroBlu. */
@Service
public class CorsaroBlu extends HtmlResultsReader implements SearchEngine {

  private final SearchResultBuilder srBuilder;

  @Value("${search.engine.corsaroBlu.baseUrl}")
  private String BASE_URL;

  public CorsaroBlu(SearchResultBuilder srBuilder) {
    this.srBuilder = srBuilder;
  }

  /**
   * Queries CorsaroBlu and returns the serialised search results.
   *
   * @param query the term to search on CorsaroBlu.
   * @param method the searching method to apply while parsing the results.
   * @return the serialised search results.
   */
  public Optional<List<SearchResult>> search(String query, SearchMethod method) {
    headers.put("authority", "ilcorsaroblu.org");
    headers.put("pragma", "no-cache");
    headers.put("cache-control", "no-cache");
    headers.put("upgrade-insecure-requests", "1");
    headers.put(
        "accept",
        "text/html,application/xhtml+xml,application/xml;q=0.9,"
            + "image/avif,image/webp,image/apng,*/*;q=0.8,"
            + "application/signed-exchange;v=b3;q=0.9");
    headers.put("sec-fetch-site", "same-origin");
    headers.put("sec-fetch-mode", "navigate");
    headers.put("sec-fetch-user", "?1");
    headers.put("sec-fetch-dest", "document");
    headers.put(
        "referer",
        "https://ilcorsaroblu.org/index.php?page=torrents&search="
            + query
            + "&category=0&options=0&active=0");
    headers.put("accept-language", "en-US,en;q=0.9");
    headers.put(
        "cookie",
        "__cfduid=d59941bb03894f0f16af8fffe58bf30a61616703035; xbtitFM=adaf9d3056e74d572474a2a95e4221da; "
            + "__utma=71060040.200814639.1616703038.1616703038.1616703038.1; __utmc=71060040; "
            + "__utmz=71060040.1616703038.1.1.utmcsr=google|utmccn=(organic)|utmcmd=organic|utmctr=(not%20provided); "
            + "_ga=GA1.2.200814639.1616703038; _gid=GA1.2.1512835960.1616703039; "
            + "fpestid=x8Fh2P4JvF0wSD4NmwKrtu-C2OJF0bNAmfvYIihIh6Ly9biVrwcbZf9MTff_r2dH7xaaww; "
            + "uid=33737; pass=66c85d83fbf856698c299d3b5374cee4; "
            + "a=wiHDmMrLbUIlue0558SeJI8HlSBGqUH7; _popfired_expires=Invalid%20Date; ajax_poller_1=1; "
            + "_popfiredinpage_expires=Invalid%20Date; _popfiredinpage=4; lastOpenAt_inpage=1616704195183; "
            + "_popfired=5; lastOpenAt_=1616704243991; __utmt=1; "
            + "token_QpUJAAAAAAAAGu98Hdz1l_lcSZ2rY60Ajjk9U1c=BAYAYFzvfgFgXPR7gAGBAsAAINWwQhq10jrc3NpiDS4VMNuJChQ7"
            + "-2Otc3-Fb7RpmvDWwQBGMEQCIBua-wRJ8gXH0hiIXvbY_TZjSg9YLHFiAH4JEpm87oXxAiBBKZOMYwTcIaSpCCYqb0oXaDGhFF"
            + "PBIL5q9X6lI7jIDA; __utmb=71060040.38.10.1616703038");

    logger.info("Searching for \"{}\" on {} ({})..", query, className, method);
    String url = BASE_URL + query + "&&order=5&by=2";
    String rowSelector =
        "#Mcol .block .block-content div div div table tbody table tbody tr:nth-child(2n+3)";
    return getResults(url, rowSelector, method);
  }

  /**
   * Parses a SearchResult object from an HTML table row.
   *
   * @param row an HTML row of the search page.
   * @return the serialised SearchResult object.
   */
  protected SearchResult parseRow(Element row) {
    logger.info("Parsing row..");
    String NAME_SELECTOR = "td:nth-child(2) > a";
    String SEEDERS_SELECTOR = "td:nth-child(6)";
    String SIZE_SELECTOR = "td:nth-child(10)";
    String HASH_SELECTOR = "td:nth-child(4) > a";
    String name = row.select(NAME_SELECTOR).text();
    String hash =
        row.select(HASH_SELECTOR)
            .attr("href")
            .replace("download.php?id=", "")
            .replace("magnet:?xt=urn:btih:", "");
    hash = hash.substring(0, Math.min(hash.length(), 40));
    String seeders = row.select(SEEDERS_SELECTOR).text();
    String size = row.select(SIZE_SELECTOR).text();
    if (name.isEmpty() | seeders.isEmpty() | hash.isEmpty()) {
      logger.error("Failed to parse required information from row: {}", row);
      return null;
    } else {
      return srBuilder
          .setName(name)
          .setSeeders(Integer.parseInt(seeders))
          .setSize(size)
          .setInfoHash(hash)
          .setOrigin(this.getClass().getSimpleName())
          .build();
    }
    // TODO: Parse all available pages.
  }
}
