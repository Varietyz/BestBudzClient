package com.bestbudz.data;

public class AccountData {

  public final int rank;

  public int uses;

  public final String username;

  public AccountData(int rank, String username) {
    this.rank = rank;
    this.username = username;
  }

  public AccountData(int rank, int uses, String username) {
    this.rank = rank;
    this.uses = uses + 1;
    this.username = username;
  }
}
