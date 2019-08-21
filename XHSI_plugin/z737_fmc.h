/**
 * z737_fmc.h
 *
 * Created on: 20 août 2019
 *
 * This code is managing Zibo Mod FMC for Laminar Boeing 737-800
 *
 * Copyright (C) 2019  Nicolas Carel
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

#ifndef Z737_FMC_H_
#define Z737_FMC_H_

int createZibo737ExtendedFmsPackets(void);

float sendZibo737ExtendedFmsCallback(
									float	inElapsedSinceLastCall,
									float	inElapsedTimeSinceLastFlightLoop,
									int		inCounter,
									void *	inRefcon);

#endif /* Z737_FMC_H_ */
