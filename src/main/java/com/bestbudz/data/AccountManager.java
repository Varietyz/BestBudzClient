package com.bestbudz.data;

import com.bestbudz.cache.Signlink;
import java.io.*;
import java.util.*;

public class AccountManager {

  private static final File saveAccountFile = new File(Signlink.findCacheDir() + "/accounts.dat");

  public static List<AccountData> accounts = new LinkedList<AccountData>();

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
      for (int size = 0; size < accounts.size(); size++) {
        if (accounts.get(size).username.equalsIgnoreCase(account.username)) {
          accounts.get(size).uses += 1;
          saveAccount();
          return;
        }
      }
    }
    if (account.username == null && account.username.length() <= 0
        || account.password == null && account.password.length() <= 0) {
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
      DataOutputStream output = new DataOutputStream(new FileOutputStream(saveAccountFile));
      output.writeByte(accounts.size());
      for (int index = 0; index < accounts.size(); index++) {
        AccountData account = accounts.get(index);
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
      DataInputStream input = new DataInputStream(new FileInputStream(saveAccountFile));
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
