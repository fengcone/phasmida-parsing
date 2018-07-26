package com.fengcone.phasmida.parsing;

import com.fengcone.phasmida.core.PhasmidaContext;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
public class ParsingContext {
    private PhasmidaContext phasmidaContext;
    private String parsingString;
    private List<ParsingNode> successNodeList = new ArrayList<ParsingNode>();
    private int nowIndex;
    private Map<Long, Integer> startIndexMap = new LinkedHashMap<Long, Integer>();
    private boolean isOpenCross = true;
    private Map<Long, Integer> processedMap = new LinkedHashMap<Long, Integer>();
    private Map<Long, Integer> endIndexMap = new LinkedHashMap<Long, Integer>();
    private Set<ParsingNode> successFatherNodeSet = new HashSet<ParsingNode>();
    private Set<Long> processedGrandson = new HashSet<Long>();
    private boolean isNeedCross;

    public void reset() {
        phasmidaContext = null;
        parsingString = null;
        successNodeList.clear();
        nowIndex = 0;
        startIndexMap.clear();
        isOpenCross = true;
        processedMap.clear();
        endIndexMap.clear();
        successFatherNodeSet.clear();
        processedGrandson.clear();
        isNeedCross = false;
    }

    private ParsingTree parsingTree;

    public void initPhasmidaContext() {
        if (phasmidaContext == null) {
            phasmidaContext = new PhasmidaContext(parsingString);
        } else {
            nowIndex = phasmidaContext.getEndIndex();
            phasmidaContext.setNextNeedBeHead(true);
        }
    }

    public void setNowIndex(int nowIndex) {
        this.nowIndex = nowIndex;
        if (phasmidaContext != null) {
            phasmidaContext.setStartIndex(0);
            phasmidaContext.setEndIndex(nowIndex);
        }
    }

    public boolean parsing() {
        return parsingTree.getRootParsingNode().process(this);
    }
}
