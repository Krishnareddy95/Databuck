package com.databuck.bean;

public class ListDmCriteria {
private Long idDm;
private Long idlistDMCriteria;
private String leftSideExp;
private String rightSideExp;
private Long idLeftColumn;
private Long idRightColumn;
private String leftSideColumn;
private String rightSideColumn;
private String matchType;
private String rightColumn;
public String getLeftSideExp() {
	return leftSideExp;
}
public void setLeftSideExp(String leftSideExp) {
	this.leftSideExp = leftSideExp;
}
public String getRightSideExp() {
	return rightSideExp;
}
public void setRightSideExp(String rightSideExp) {
	this.rightSideExp = rightSideExp;
}
public String getMatchType() {
	return matchType;
}
public void setMatchType(String matchType) {
	this.matchType = matchType;
}
public Long getIdDm() {	return idDm;}
public void setIdDm(Long idDm) {
	this.idDm = idDm;
}
public Long getIdlistDMCriteria() {
	return idlistDMCriteria;
}
public void setIdlistDMCriteria(Long idlistDMCriteria) {
	this.idlistDMCriteria = idlistDMCriteria;
}
public Long getIdLeftColumn() {return idLeftColumn;	}
public void setIdLeftColumn(Long idLeftColumn) {this.idLeftColumn = idLeftColumn;}
public String getRightSideColumn() {return rightSideColumn;	}
public void setRightSideColumn(String rightSideColumn) {this.rightSideColumn = rightSideColumn;	}
public Long getIdRightColumn() {return idRightColumn;}
public void setIdRightColumn(Long idRightColumn) {this.idRightColumn = idRightColumn;}
public String getLeftSideColumn() {return leftSideColumn;}
public void setLeftSideColumn(String leftSideColumn) {this.leftSideColumn = leftSideColumn;	}
public String getRightColumn() {return rightColumn;	}
public void setRightColumn(String rightColumn) {this.rightColumn = rightColumn;	}

}
