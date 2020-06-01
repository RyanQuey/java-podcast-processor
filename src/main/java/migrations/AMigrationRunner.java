package migrations;

// put A at start so at the top of list alphabetically
public class AMigrationRunner {
  // TODO find more elegant solution for running migrations so can run independently of connecting to db, perhaps a separate jar file I can run or something
  // NOTE currently not maintaining external versioning system for this (eg a separate file or a db table), not really necessary yet
  // since doing IF NOT EXISTS then can run all these all the time we want to migrate
  public static void runMigrations () throws Exception {
    M20200513211500CreateKeyspace.run(); 
    M20200513221500CreateSearchQueriesTable.run();
    M20200520161500CreateSearchQueriesUDT.run();
    M20200524201500CreatePodcastsTable.run(); 
    M20200529151500CreateEpisodesByPodcastTable.run();
    M20200606161500AddPodcastCountToSearchQueriesByTerm.run();


    System.out.println("***finished running migrations***");
  }
}
