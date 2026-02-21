package com.bestbudz.data;

import com.bestbudz.cache.Signlink;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class AccountManager {

  private static final String DIR = Signlink.findCacheDir();
  private static final String PATH = DIR + "accounts.json";
  private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

  public static final List<AccountData> accounts = new LinkedList<>();

  public static List<AccountData> getAccounts() {
    return accounts;
  }

  public static AccountData getAccount(String username) {
    for (AccountData account : accounts) {
      if (account.username.equalsIgnoreCase(username)) {
        return account;
      }
    }
    return null;
  }

  public static void addAccount(AccountData account) {
    if (!accounts.isEmpty()) {
		for (AccountData accountData : accounts)
		{
			if (accountData.username.equalsIgnoreCase(account.username))
			{
				accountData.uses += 1;
				saveAccount();
				return;
			}
		}
    }
    if (account.username == null || account.username.length() == 0) {
      return;
    }
    accounts.add(account);
    saveAccount();
  }

  public static void clearAccountList() {
    accounts.clear();
    saveAccount();
  }

  public static void removeAccount(AccountData account) {
    if (!accounts.contains(account)) {
      return;
    }
    accounts.remove(account);
    saveAccount();
  }

  public static void saveAccount() {
    if (accounts == null || accounts.isEmpty()) {
      return;
    }
    try {
      JsonArray arr = new JsonArray();
      for (AccountData account : accounts)
      {
        JsonObject obj = new JsonObject();
        obj.addProperty("rank", account.rank);
        obj.addProperty("uses", account.uses);
        obj.addProperty("username", account.username);
        arr.add(obj);
      }
      Files.write(Paths.get(PATH), GSON.toJson(arr).getBytes(StandardCharsets.UTF_8));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void loadAccount() {
    File jsonFile = new File(PATH);

    if (!jsonFile.exists()) {
      return;
    }

    try {
      String content = new String(Files.readAllBytes(Paths.get(PATH)), StandardCharsets.UTF_8);
      JsonArray arr = GSON.fromJson(content, JsonArray.class);
      for (JsonElement el : arr) {
        JsonObject obj = el.getAsJsonObject();
        int rank = obj.has("rank") ? obj.get("rank").getAsInt() : 0;
        int uses = obj.has("uses") ? obj.get("uses").getAsInt() : 0;
        String username = obj.has("username") ? obj.get("username").getAsString() : "";
        AccountData account = new AccountData(rank, uses, username);
        accounts.add(account);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
