package com.bestbudz.data;

import com.bestbudz.cache.Signlink;
import java.io.*;
import java.nio.file.Files;
import java.util.*;

public class AccountManager {

  private static final File saveAccountFile = new File(Signlink.findCacheDir() + "/accounts.dat");

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
    if (account.username == null && account.username.length() == 0
        || account.password == null && account.password.length() == 0) {
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
      DataOutputStream output = new DataOutputStream(Files.newOutputStream(saveAccountFile.toPath()));
      output.writeByte(accounts.size());
		for (AccountData account : accounts)
		{
			output.writeInt(account.rank);
			output.writeInt(account.uses);
			output.writeUTF(account.username);
			output.writeUTF(account.password);
		}
      output.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void loadAccount() {
    if (!saveAccountFile.exists()) {
      return;
    }
    try {
      DataInputStream input = new DataInputStream(Files.newInputStream(saveAccountFile.toPath()));
      int fileSize = input.readByte();
      for (int index = 0; index < fileSize; index++) {
        int rank = input.readInt();
        int uses = input.readInt();
        String username = input.readUTF();
        String password = input.readUTF();
        AccountData account = new AccountData(rank, uses, username, password);
        accounts.add(account);
      }
      input.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
