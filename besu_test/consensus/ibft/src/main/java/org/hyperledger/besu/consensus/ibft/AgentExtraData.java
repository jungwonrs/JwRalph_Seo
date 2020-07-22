package org.hyperledger.besu.consensus.ibft;

import org.hyperledger.besu.ethereum.core.ParsedExtraData;


import static com.google.common.base.Preconditions.checkNotNull;

import org.hyperledger.besu.crypto.SECP256K1.Signature;
import org.hyperledger.besu.ethereum.core.Address;
import org.hyperledger.besu.ethereum.core.BlockHeader;
import org.hyperledger.besu.ethereum.rlp.BytesValueRLPInput;
import org.hyperledger.besu.ethereum.rlp.BytesValueRLPOutput;
import org.hyperledger.besu.ethereum.rlp.RLPInput;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tuweni.bytes.Bytes;



public class AgentExtraData implements ParsedExtraData {
    private static final Logger LOG = LogManager.getLogger();

    public static final int EXTRA_VANITY_LENGTH = 32;

    private final Bytes vanityData;
    private final Collection<Signature> seals;
    private final Optional<Vote> vote;
    private final int round;
    private final Collection<Address> validators;

    public AgentExtraData(
            final Bytes vanityData,
            final Collection<Signature> seals,
            final Optional<Vote> vote,
            final int round,
            final Collection<Address> validators
    ){
        checkNotNull(vanityData);
        checkNotNull(seals);
        checkNotNull(validators);

        this.vanityData = vanityData;
        this.seals = seals;
        this.round = round;
        this.validators = validators;
        this.vote = vote;
    }

    public static AgentExtraData fromAddresses(final Collection<Address> addresses){
        return new AgentExtraData(
                Bytes.wrap(new byte[32]), Collections.emptyList(), Optional.empty(), 0, addresses);
    }

    public static AgentExtraData decode (final BlockHeader blockHeader){
        final Object inputExtraData = blockHeader.getParsedExtraData();
        if (inputExtraData instanceof AgentExtraData){
            return (AgentExtraData) inputExtraData;
        }
        LOG.warn(
                "Expected a AgentExtraData instance but got {}. Reparsing required",
                inputExtraData != null ? inputExtraData.getClass().getSimpleName() : "null");
        return decodeRaw(blockHeader.getExtraData());
    }

    static AgentExtraData decodeRaw(final Bytes input){
        if (input.isEmpty()){
            throw new IllegalArgumentException("Invalid Bytes supplied - Agent Extra Data Required.");
        }
        final RLPInput rlpInput =  new BytesValueRLPInput(input, false);

        rlpInput.enterList();
        final Bytes vanityData = rlpInput.readBytes();
        final List<Address> validators = rlpInput.readList(Address::readFrom);
        final Optional<Vote> vote;
        if (rlpInput.nextIsNull()){
            vote = Optional.empty();
            rlpInput.skipNext();
        }else {
            vote = Optional.of(Vote.readFrom((rlpInput)));
        }
        final int round = rlpInput.readInt();
        final List<Signature> seals = rlpInput.readList(rlp -> Signature.decode(rlp.readBytes()));
        rlpInput.leaveList();
        return new AgentExtraData(vanityData, seals, vote, round, validators);
    }

    public Bytes encode() {return encode(EncodingType.ALL);}

    public Bytes encodeWithoutCommitSeals() {return encode(EncodingType.EXCLUDE_COMMIT_SEALS);}

    public Bytes encodeWithoutCommitSealsAndRoundNumber(){
        return encode(EncodingType.EXCLUDE_COMMIT_SEALS_AND_ROUND_NUMBER);
    }

    private enum EncodingType{
        ALL,
        EXCLUDE_COMMIT_SEALS,
        EXCLUDE_COMMIT_SEALS_AND_ROUND_NUMBER
    }

    private Bytes encode(final EncodingType encodingType){
        final BytesValueRLPOutput encoder = new BytesValueRLPOutput();
        encoder.startList();
        encoder.writeBytes(vanityData);
        encoder.writeList(validators, (validator, rlp) -> rlp.writeBytes(validator));
        if (vote.isPresent()) {
            vote.get().writeTo(encoder);
        } else {
            encoder.writeNull();
        }

        if (encodingType != AgentExtraData.EncodingType.EXCLUDE_COMMIT_SEALS_AND_ROUND_NUMBER) {
            encoder.writeInt(round);
            if (encodingType != AgentExtraData.EncodingType.EXCLUDE_COMMIT_SEALS) {
                encoder.writeList(seals, (committer, rlp) -> rlp.writeBytes(committer.encodedBytes()));
            }
        }
        encoder.endList();

        return encoder.encoded();
    }
    public static String createGenesisExtraDataString(final List<Address> validators) {
        final AgentExtraData extraData =
                new AgentExtraData(
                        Bytes.wrap(new byte[32]), Collections.emptyList(), Optional.empty(), 0, validators);
        return extraData.encode().toString();
    }

    // Accessors
    public Bytes getVanityData() {
        return vanityData;
    }

    public Collection<Signature> getSeals() {
        return seals;
    }

    public Collection<Address> getValidators() {
        return validators;
    }

    public Optional<Vote> getVote() {
        return vote;
    }

    public int getRound() {
        return round;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", IbftExtraData.class.getSimpleName() + "[", "]")
                .add("vanityData=" + vanityData)
                .add("seals=" + seals)
                .add("vote=" + vote)
                .add("round=" + round)
                .add("validators=" + validators)
                .toString();
    }
}