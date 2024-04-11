
public class AmaxJavaDemo {
    public static void main(String[] args) throws Exception {

        ChainInfo info = mgpBlockInfo().getInfo();
        Tx tx = mgpBlockInfo().getTx();
        List<TxAction> actions = new ArrayList();
        tx.setActions(actions);
        Map<String, Object> map = new LinkedHashMap();
        map.put("from", "public key"); //public key
        map.put("to", "apl******"); // amc account
        map.put("quantity", new DataParam(quantity, DataType.asset, Action.transfer).getValue());
        map.put("memo", memo);
        TxAction setBindAction = new TxAction(from, "amax.mtoken", "tranfer", map);
        actions.add(setBindAction);

        String sign = Ecc.signTransaction("${private key}", new TxSign(info.getChainId(), tx));

        String data = Ecc.parseParamsData(
                new DataParam(from, DataType.name, Action.transfer),
                new DataParam(contractConfig.getXdaoClaim(), DataType.name, Action.transfer),
                new DataParam(quantity, DataType.asset, Action.transfer),
                new DataParam(memo, DataType.string, Action.transfer));
        setBindAction.setData(data);

        tx.setExpiration(this.dateFormatter.format(new Date(1000L * Long.parseLong(tx.getExpiration().toString()))));
        return pushTrx(tx, sign);

    }
}