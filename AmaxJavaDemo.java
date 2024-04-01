import io.eblock.eos4j.Ecc;
import io.eblock.eos4j.Rpc;
import io.eblock.eos4j.api.vo.Block;
import io.eblock.eos4j.api.vo.ChainInfo;
import io.eblock.eos4j.api.vo.transaction.push.Tx;
import io.eblock.eos4j.api.vo.transaction.push.TxAction;
import io.eblock.eos4j.api.vo.transaction.push.TxSign;
import io.eblock.eos4j.ese.Action;
import io.eblock.eos4j.ese.DataParam;
import io.eblock.eos4j.ese.DataType;
import io.eblock.eos4j.ese.Ese;

import java.util.*;

public class AmaxJavaDemo {

    public static void main(String[] args) throws Exception {

        Rpc rpc = new Rpc("https://******");

        String creator = "amax";
        long buyRamBytes = 0L;

        String stakeNetAsset = "0.11000000 AMAX"; //The amount of tokens delegated for net bandwidth
        String stakeCpuAsset = "0.11000000 AMAX"; //The amount of tokens delegated for CPU bandwidth
        Long transfer = 1L;

        ChainInfo info = rpc.getChainInfo();
        Block block = rpc.getBlock(info.getLastIrreversibleBlockNum().toString());

        String newAccount = "";
        String owner = "";
        String active = "";

        Tx tx = new Tx();
        tx.setExpiration(info.getHeadBlockTime().getTime() / 1000L + 60L);
        tx.setRef_block_num(info.getLastIrreversibleBlockNum());
        tx.setRef_block_prefix(block.getRefBlockPrefix());
        tx.setNet_usage_words(0L);
        tx.setMax_cpu_usage_ms(0L);
        tx.setDelay_sec(0L);
        List<TxAction> actions = new ArrayList();
        tx.setActions(actions);
        Map<String, Object> createMap = new LinkedHashMap();
        createMap.put("creator", creator);
        createMap.put("name", newAccount); //accountname
        createMap.put("owner", owner); //owner
        createMap.put("active", ""); //active pub key
        TxAction createAction = new TxAction(creator, "amax", "newaccount", createMap);
        actions.add(createAction);

        //buy ram
        Map<String, Object> buyMap = new LinkedHashMap();
        buyMap.put("payer", creator);
        buyMap.put("receiver", newAccount);//accountname
        buyMap.put("bytes", buyRamBytes);
        TxAction buyAction = new TxAction(creator, "amax", "buyrambytes", buyMap);
        actions.add(buyAction);

        // stake cpu
        Map<String, Object> delMap = new LinkedHashMap();
        delMap.put("from", creator);
        delMap.put("receiver", newAccount);//accountname
        delMap.put("stake_net_quantity", (new DataParam(stakeNetAsset, DataType.asset, Action.delegate)).getValue());
        delMap.put("stake_cpu_quantity", (new DataParam(stakeCpuAsset, DataType.asset, Action.delegate)).getValue());
        delMap.put("transfer", transfer);

        TxAction delAction = new TxAction(creator, "amax", "delegatebw", delMap);
        actions.add(delAction);

        //active account private key
        String sign = Ecc.signTransaction("", new TxSign(info.getChainId(), tx));

        String accountData = Ese.parseParamsData(new DataParam(creator, DataType.name, Action.account), new DataParam(newAccount, DataType.name, Action.account), new DataParam(owner, DataType.key, Action.account), new DataParam(active, DataType.key, Action.account));
        createAction.setData(accountData);
        String ramData = Ese.parseParamsData(new DataParam(creator, DataType.name, Action.ram), new DataParam(newAccount, DataType.name, Action.ram), new DataParam(String.valueOf(buyRamBytes), DataType.unit32, Action.ram));
        buyAction.setData(ramData);
        String delData = Ese.parseParamsData(new DataParam(creator, DataType.name, Action.delegate), new DataParam(newAccount, DataType.name, Action.delegate), new DataParam(stakeNetAsset, DataType.asset, Action.delegate), new DataParam(stakeCpuAsset, DataType.asset, Action.delegate), new DataParam(String.valueOf(transfer.intValue()), DataType.varint32, Action.delegate));
        delAction.setData(delData);
        tx.setExpiration(rpc.format(new Date(1000L * Long.parseLong(tx.getExpiration().toString()))));
        rpc.pushTransaction("none", tx, new String[]{sign});
    }
}
