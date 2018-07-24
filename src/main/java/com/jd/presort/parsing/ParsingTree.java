package com.jd.presort.parsing;

import com.fengcone.phasmida.core.PhasmidaFactory;
import lombok.Getter;
import lombok.Setter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class ParsingTree {
    private Map<Long, ParsingNode> nodesMap;
    private Map<Long, List<ParsingNode>> childrenListMap;
    private ParsingNode rootParsingNode;
    private List<ParsingNode> rootParsingNodes;


    public void init(List<ParsingNode> parsingNodes) {
        nodesMap = new HashMap<Long, ParsingNode>();
        childrenListMap = new HashMap<Long, List<ParsingNode>>();
        for (ParsingNode parsingNode : parsingNodes) {
            nodesMap.put(parsingNode.getId(), parsingNode);
            long fatherId = parsingNode.getFatherId();
            List<ParsingNode> childrenList = childrenListMap.get(fatherId);
            if (childrenList == null) {
                childrenList = new ArrayList<ParsingNode>();
                childrenListMap.put(fatherId, childrenList);
            }
            childrenList.add(parsingNode);
        }
        rootParsingNodes = childrenListMap.get(ParsingConstant.ROOT_NODE_ID);
        rootParsingNode = rootParsingNodes.get(0);
        setChildrenNode(rootParsingNodes);
    }

    private void setChildrenNode(List<ParsingNode> rootNodes) {
        ParsingNode lastNode = null;
        for (ParsingNode rootNode : rootNodes) {
            if (lastNode != null) {
                lastNode.setNextNode(rootNode);
            }
            lastNode = rootNode;
            List<ParsingNode> parsingNodes = childrenListMap.get(rootNode.getId());
            if (parsingNodes == null || parsingNodes.size() == 0) {
                continue;
            }
            rootNode.setChildrenNode(parsingNodes);
            setChildrenNode(parsingNodes);
        }
    }
}
