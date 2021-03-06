/*
 *  Copyright 2010 BetaSteward_at_googlemail.com. All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without modification, are
 *  permitted provided that the following conditions are met:
 *
 *     1. Redistributions of source code must retain the above copyright notice, this list of
 *        conditions and the following disclaimer.
 *
 *     2. Redistributions in binary form must reproduce the above copyright notice, this list
 *        of conditions and the following disclaimer in the documentation and/or other materials
 *        provided with the distribution.
 *
 *  THIS SOFTWARE IS PROVIDED BY BetaSteward_at_googlemail.com ``AS IS'' AND ANY EXPRESS OR IMPLIED
 *  WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 *  FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL BetaSteward_at_googlemail.com OR
 *  CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 *  CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 *  SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 *  ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *  The views and conclusions contained in the software and documentation are those of the
 *  authors and should not be interpreted as representing official policies, either expressed
 *  or implied, of BetaSteward_at_googlemail.com.
 */
package mage.cards.a;

import java.util.UUID;
import mage.abilities.Ability;
import mage.abilities.common.BeginningOfUpkeepTriggeredAbility;
import mage.abilities.dynamicvalue.common.StaticValue;
import mage.abilities.effects.Effect;
import mage.abilities.effects.OneShotEffect;
import mage.abilities.effects.common.ExileSpellEffect;
import mage.abilities.effects.common.counter.AddCountersSourceEffect;
import mage.cards.*;
import mage.constants.CardType;
import mage.constants.Outcome;
import mage.constants.TargetController;
import mage.constants.Zone;
import mage.counters.CounterType;
import mage.filter.common.FilterCreatureCard;
import mage.game.Game;
import mage.players.Player;

/**
 *
 * @author jeffwadsworth
 */
public class AllHallowsEve extends CardImpl {

    public AllHallowsEve(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.SORCERY}, "{2}{B}{B}");

        // Exile All Hallow's Eve with two scream counters on it.
        this.getSpellAbility().addEffect(ExileSpellEffect.getInstance());
        Effect effect = new AddCountersSourceEffect(CounterType.SCREAM.createInstance(), new StaticValue(2), true, true);
        effect.setText("with 2 scream counters on it");
        this.getSpellAbility().addEffect(effect);

        // At the beginning of your upkeep, if All Hallow's Eve is exiled with a scream counter on it, remove a scream counter from it. If there are no more scream counters on it, put it into your graveyard and each player returns all creature cards from his or her graveyard to the battlefield.
        this.addAbility(new BeginningOfUpkeepTriggeredAbility(Zone.EXILED, new AllHallowsEveEffect(), TargetController.YOU, false));

    }

    public AllHallowsEve(final AllHallowsEve card) {
        super(card);
    }

    @Override
    public AllHallowsEve copy() {
        return new AllHallowsEve(this);
    }
}

class AllHallowsEveEffect extends OneShotEffect {

    public AllHallowsEveEffect() {
        super(Outcome.PutCreatureInPlay);
        this.staticText = "if {this} is exiled with a scream counter on it, remove a scream counter from it. If there are no more scream counters on it, put it into your graveyard and each player returns all creature cards from his or her graveyard to the battlefield";
    }

    public AllHallowsEveEffect(final AllHallowsEveEffect effect) {
        super(effect);
    }

    @Override
    public AllHallowsEveEffect copy() {
        return new AllHallowsEveEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Card allHallowsEve = game.getCard(source.getSourceId());
        Player controller = game.getPlayer(source.getControllerId());
        if (allHallowsEve != null
                && controller != null
                && game.getExile().getCard(allHallowsEve.getId(), game) != null) {
            allHallowsEve.getCounters(game).removeCounter(CounterType.SCREAM, 1);
            if (allHallowsEve.getCounters(game).getCount(CounterType.SCREAM) == 0) {
                allHallowsEve.moveToZone(Zone.GRAVEYARD, source.getId(), game, false);
                Cards creatures = new CardsImpl();
                for (UUID playerId : game.getState().getPlayersInRange(controller.getId(), game)) {
                    Player player = game.getPlayer(playerId);
                    if (player != null) {
                        for (Card creatureCard : player.getGraveyard().getCards(new FilterCreatureCard(), game)) {
                            creatures.add(creatureCard);
                        }
                    }
                }
                for (Card card : creatures.getCards(game)) {
                    card.putOntoBattlefield(game, Zone.GRAVEYARD, source.getId(), card.getOwnerId());
                }
            }
            return true;
        }
        return false;
    }
}
