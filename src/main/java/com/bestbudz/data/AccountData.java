package com.bestbudz.data;

public class AccountData {

  public int rank;

  public int uses;

  public String username;

  public String password;

  public AccountData(int rank, String username, String password) {
    this.rank = rank;
    this.username = username;
    this.password = password;
  }

  public AccountData(int rank, int uses, String username, String password) {
    this.rank = rank;
    this.uses = uses;
    this.username = username;
    this.password = password;
  }
}
