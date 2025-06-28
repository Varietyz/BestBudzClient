package com.bestbudz.data;

public class AccountData {

  public final int rank;

  public int uses;

  public final String username;

  public final String password;

  public AccountData(int rank, String username, String password) {
    this.rank = rank;
    this.username = username;
    this.password = password;
  }

  public AccountData(int rank, int uses, String username, String password) {
    this.rank = rank;
    this.uses = uses + 1;
    this.username = username;
    this.password = password;
  }
}
