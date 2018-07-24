package com.jd.presort.parsing;

import com.fengcone.phasmida.core.Phasmida;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ParsingNode {
    private boolean reIndex;
    private boolean reFatherIndex;
    private boolean dengCross;
    private Phasmida phasmida;
    private String description;
    private String targetId;
    private long id;
    private long fatherId;
    private int parsingLevel;
    private List<ParsingNode> childrenNode;
    private ParsingNode nextNode;


    public boolean process(ParsingContext context) {
        handleStartIndex(context);
        int nowIndex = context.getNowIndex();
        Integer processedResult = context.getProcessedMap().get(id);
        if (processedResult != null && Math.abs(processedResult) <= nowIndex) {
            return false;
        }
        boolean result = false;
        if (judgeCross(context)) {
            context.initPhasmidaContext();
            result = phasmida.process(context.getPhasmidaContext());
            context.getProcessedMap().put(id, result ? (nowIndex == 0 ? ParsingConstant.SPECIAL_INDEX : nowIndex) : -nowIndex);
        }
        if (result) {
            handleSuccessNode(context, nowIndex);
            if (isLastNode(this)) {
                return true;
            }
            if (reIndex) {
                context.setNowIndex(nowIndex);
            }
            if (childrenNode.get(0).process(context)) {
                return true;
            }
        }
        context.setNowIndex(nowIndex);
        if (nextNode == null) {
            if (context.isNeedCross() || fatherId == ParsingConstant.ROOT_NODE_ID) {
                return handleCrossProcess(context);
            }
            context.setNeedCross(true);
            return false;
        }
        return nextNode.process(context);
    }

    private boolean handleCrossProcess(ParsingContext context) {
        if (context.isOpenCross()) {
            return false;
        }
        context.setOpenCross(false);
        if (context.getSuccessNodeList().size() == 0) {
            context.getProcessedGrandson().add(ParsingConstant.ROOT_NODE_ID);
            context.setNowIndex(0);
            boolean rootResult = handleGrandsons(context.getParsingTree().getRootParsingNodes(), context);
            if (rootResult) {
                return true;
            }
        }
        int successCount = context.getSuccessNodeList().size();
        if (successCount == 0) {
            return false;
        }

        for (int i = successCount - 1; i >= 0; i--) {
            ParsingNode parsingNode = context.getSuccessNodeList().get(i);
            if (context.getProcessedGrandson().contains(parsingNode.getId())) {
                continue;
            }
            context.getProcessedGrandson().add(parsingNode.getId());
            context.setNowIndex(context.getEndIndexMap().get(parsingNode.getId()));
            boolean grandsonsResult = handleGrandsons(parsingNode.getChildrenNode(), context);
            if (grandsonsResult) {
                return true;
            }
            if (successCount != context.getSuccessNodeList().size()) {
                context.setOpenCross(true);
                return handleCrossProcess(context);
            }
        }
        if (!context.getProcessedGrandson().contains(ParsingConstant.ROOT_NODE_ID)) {
            context.getProcessedGrandson().add(ParsingConstant.ROOT_NODE_ID);
            context.setNowIndex(0);
            boolean rootResult = handleGrandsons(context.getParsingTree().getRootParsingNodes(), context);
            if (rootResult) {
                return true;
            }
            if (successCount != context.getSuccessNodeList().size()) {
                context.setOpenCross(true);
                return handleCrossProcess(context);
            }
        }
        return false;
    }

    private boolean handleGrandsons(List<ParsingNode> list, ParsingContext context) {
        if (list == null || list.size() == 0) {
            return false;
        }
        for (ParsingNode parsingNode : list) {
            if (isLastNode(parsingNode)) {
                continue;
            }
            boolean grandsonResult = parsingNode.getChildrenNode().get(0).process(context);
            if (grandsonResult) {
                return true;
            }
        }
        return false;
    }

    private boolean isLastNode(ParsingNode node) {
        return node.getChildrenNode() == null || node.getChildrenNode().size() == 0;
    }


    private void handleSuccessNode(ParsingContext context, int nowIndex) {
        context.setNowIndex(context.getPhasmidaContext().getEndIndex());
        context.getSuccessNodeList().add(this);
        context.getStartIndexMap().put(id, nowIndex);
        context.getEndIndexMap().put(id, context.getNowIndex());
        ParsingNode parsingNode = context.getParsingTree().getNodesMap().get(fatherId);
        if (parsingNode != null) {
            context.getSuccessFatherNodeSet().add(parsingNode);
        }
    }

    private boolean judgeCross(ParsingContext context) {
        if (!dengCross) {
            return true;
        }
        if (context.getSuccessNodeList().size() == 0) {
            return false;
        }
        ParsingNode fatherNode = context.getSuccessNodeList().get(context.getSuccessNodeList().size() - 1);
        if (fatherId == fatherNode.getId()) {
            return true;
        }
        return false;
    }


    private void handleStartIndex(ParsingContext context) {
        if (!reFatherIndex) {
            return;
        }
        if (context.getSuccessNodeList().size() == 0) {
            return;
        }
        Long id = context.getSuccessNodeList().get(context.getSuccessNodeList().size() - 1).getId();
        Integer startIndex = context.getStartIndexMap().get(id);
        if (startIndex != null && startIndex >= 0 && !context.isOpenCross()) {
            context.setNowIndex(startIndex);
        }
    }
}
