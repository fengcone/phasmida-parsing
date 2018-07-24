package com.jd.presort.parsing.response;

import com.jd.presort.parsing.ParsingContext;
import com.jd.presort.parsing.ParsingNode;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class AnalyzerResponseUtil {
    public static AnalyzerResponse generateResponse(ParsingContext context) {
        AnalyzerResponse response = new AnalyzerResponse();
        response.setAddress(context.getParsingString());
        List<ParsingNode> successNodes = context.getSuccessNodeList();
        Set<ParsingNode> successFatherNodeSet = context.getSuccessFatherNodeSet();
        int level = 4;
        if (successNodes.size() == 0) {
            return response;
        }
        ArrayList<Set<ParsingNode>> rules = new ArrayList<Set<ParsingNode>>();
        for (int i = 0; i < level; i++) {
            rules.add(new LinkedHashSet<ParsingNode>());
        }
        for (ParsingNode parsingNode : successNodes) {
            if (parsingNode.getParsingLevel() > level) {
                return response;
            }

            rules.get(parsingNode.getParsingLevel() - 1).add(parsingNode);
        }
        for (ParsingNode parsingNode : successFatherNodeSet) {
            if (parsingNode.getParsingLevel() > level) {
                return response;
            }
            rules.get(parsingNode.getParsingLevel() - 1).add(parsingNode);
        }
        for (int i = level - 1; i >= 0; i--) {
            ParsingNode compareRule = compareRule(rules.get(i), null);
            if (compareRule != null) {
                setResponse(rules, response, compareRule);
                return response;
            }
        }
        return response;
    }

    private static ParsingNode compareRule(Set<ParsingNode> ruleSet, ParsingNode child) {
        List<ParsingNode> rules = new ArrayList<ParsingNode>(ruleSet);
        if (rules.size() == 1) {
            return rules.get(0);
        }
        if (rules.size() == 0) {
            return null;
        }
        if (child != null) {
            for (ParsingNode presortRules : rules) {
                if (child.getFatherId() == presortRules.getId()) {
                    return presortRules;
                }
            }
        }
        return rules.get(0);
    }

    private static void setResponse(ArrayList<Set<ParsingNode>> rules, AnalyzerResponse response, ParsingNode last) {
        if (rules == null) {
            return;
        }
        switch (last.getParsingLevel()) {
            case 4:
                response.setTownId(Integer.valueOf(last.getTargetId()));
                response.setTownName(last.getDescription());
                setResponse(rules, response, compareRule(rules.get(2), last));
                break;
            case 3:
                response.setCountyId(Integer.valueOf(last.getTargetId()));
                response.setCountyName(last.getDescription());
                setResponse(rules, response, compareRule(rules.get(1), last));
                break;
            case 2:
                response.setCityId(Integer.valueOf(last.getTargetId()));
                response.setCityName(last.getDescription());
                setResponse(rules, response, compareRule(rules.get(0), last));
                break;
            case 1:
                response.setProvinceId(Integer.valueOf(last.getTargetId()));
                response.setProvinceName(last.getDescription());
                break;
            default:
                setResponse(rules, response, compareRule(rules.get(last.getParsingLevel() - 1), last));
                break;
        }
    }
}
